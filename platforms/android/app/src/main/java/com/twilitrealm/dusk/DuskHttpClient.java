package dev.twilitrealm.dusk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public final class DuskHttpClient {
    public static final int ERROR_NONE = 0;
    public static final int ERROR_INVALID_URL = 1;
    public static final int ERROR_UNSUPPORTED_SCHEME = 2;
    public static final int ERROR_TIMEOUT = 3;
    public static final int ERROR_TOO_LARGE = 4;
    public static final int ERROR_NETWORK = 5;

    private static final int MAX_REDIRECTS = 5;

    public static final class Response {
        public int error;
        public String message;
        public int statusCode;
        public String[] headerNames;
        public String[] headerValues;
        public byte[] body;

        Response(int error, String message, int statusCode, String[] headerNames,
                 String[] headerValues, byte[] body) {
            this.error = error;
            this.message = message;
            this.statusCode = statusCode;
            this.headerNames = headerNames != null ? headerNames : new String[0];
            this.headerValues = headerValues != null ? headerValues : new String[0];
            this.body = body != null ? body : new byte[0];
        }
    }

    private DuskHttpClient() {
    }

    public static Response get(String url, String[] headerNames, String[] headerValues,
                               int timeoutMs, long maxBodyBytes) {
        if (url == null || url.isEmpty()) {
            return fail(ERROR_INVALID_URL, "URL is empty");
        }

        try {
            URL currentUrl = new URL(url);
            if (!isHttps(currentUrl)) {
                return fail(ERROR_UNSUPPORTED_SCHEME, "Only https:// URLs are supported");
            }

            for (int redirect = 0; redirect <= MAX_REDIRECTS; ++redirect) {
                HttpsURLConnection connection =
                        (HttpsURLConnection) currentUrl.openConnection();
                try {
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(timeoutMs);
                    connection.setReadTimeout(timeoutMs);
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(false);
                    applyHeaders(connection, headerNames, headerValues);

                    int statusCode = connection.getResponseCode();
                    if (isRedirect(statusCode)) {
                        String location = connection.getHeaderField("Location");
                        if (location == null || location.isEmpty()) {
                            return fail(ERROR_NETWORK, "Redirect response did not include Location",
                                    statusCode, connection, new byte[0]);
                        }

                        URL nextUrl = new URL(currentUrl, location);
                        if (!isHttps(nextUrl)) {
                            return fail(ERROR_UNSUPPORTED_SCHEME,
                                    "Only https:// redirects are supported", statusCode,
                                    connection, new byte[0]);
                        }
                        currentUrl = nextUrl;
                        continue;
                    }

                    byte[] body = readBody(connection, statusCode, maxBodyBytes);
                    return success(statusCode, connection, body);
                } catch (ResponseTooLargeException e) {
                    return fail(ERROR_TOO_LARGE, "Response body exceeded the configured limit",
                            safeStatusCode(connection), connection, e.partialBody);
                } finally {
                    connection.disconnect();
                }
            }

            return fail(ERROR_NETWORK, "Too many redirects");
        } catch (MalformedURLException e) {
            return fail(ERROR_INVALID_URL, "Failed to parse URL");
        } catch (SocketTimeoutException e) {
            return fail(ERROR_TIMEOUT, "Request timed out");
        } catch (IOException e) {
            String message = e.getMessage();
            return fail(ERROR_NETWORK, message != null ? message : e.toString());
        } catch (ClassCastException e) {
            return fail(ERROR_UNSUPPORTED_SCHEME, "Only https:// URLs are supported");
        }
    }

    private static void applyHeaders(HttpsURLConnection connection, String[] names,
                                     String[] values) {
        if (names == null || values == null) {
            return;
        }

        int count = Math.min(names.length, values.length);
        for (int i = 0; i < count; ++i) {
            if (names[i] != null && values[i] != null) {
                connection.setRequestProperty(names[i], values[i]);
            }
        }
    }

    private static boolean isHttps(URL url) {
        return "https".equalsIgnoreCase(url.getProtocol());
    }

    private static boolean isRedirect(int statusCode) {
        return statusCode == HttpURLConnection.HTTP_MOVED_PERM ||
                statusCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                statusCode == HttpURLConnection.HTTP_SEE_OTHER ||
                statusCode == 307 ||
                statusCode == 308;
    }

    private static byte[] readBody(HttpsURLConnection connection, int statusCode,
                                   long maxBodyBytes) throws IOException,
            ResponseTooLargeException {
        InputStream stream = statusCode >= HttpURLConnection.HTTP_BAD_REQUEST ?
                connection.getErrorStream() : connection.getInputStream();
        if (stream == null) {
            return new byte[0];
        }

        try (InputStream bodyStream = stream;
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            long total = 0;
            while (true) {
                int read = bodyStream.read(buffer);
                if (read < 0) {
                    return out.toByteArray();
                }
                if (read == 0) {
                    continue;
                }
                if (read > maxBodyBytes || total > maxBodyBytes - read) {
                    throw new ResponseTooLargeException(out.toByteArray());
                }
                out.write(buffer, 0, read);
                total += read;
            }
        }
    }

    private static int safeStatusCode(HttpsURLConnection connection) {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            return 0;
        }
    }

    private static Response success(int statusCode, HttpsURLConnection connection, byte[] body) {
        HeaderLists headers = readHeaders(connection);
        return new Response(ERROR_NONE, "", statusCode, headers.names, headers.values, body);
    }

    private static Response fail(int error, String message) {
        return new Response(error, message, 0, null, null, null);
    }

    private static Response fail(int error, String message, int statusCode,
                                 HttpsURLConnection connection, byte[] body) {
        HeaderLists headers = readHeaders(connection);
        return new Response(error, message, statusCode, headers.names, headers.values, body);
    }

    private static HeaderLists readHeaders(HttpsURLConnection connection) {
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();

        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (headerFields == null) {
            return new HeaderLists(new String[0], new String[0]);
        }

        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String name = entry.getKey();
            if (name == null) {
                continue;
            }
            List<String> entryValues = entry.getValue();
            if (entryValues == null || entryValues.isEmpty()) {
                names.add(name);
                values.add("");
                continue;
            }
            for (String value : entryValues) {
                names.add(name);
                values.add(value != null ? value : "");
            }
        }

        return new HeaderLists(names.toArray(new String[0]), values.toArray(new String[0]));
    }

    private static final class HeaderLists {
        final String[] names;
        final String[] values;

        HeaderLists(String[] names, String[] values) {
            this.names = names;
            this.values = values;
        }
    }

    private static final class ResponseTooLargeException extends Exception {
        final byte[] partialBody;

        ResponseTooLargeException(byte[] partialBody) {
            this.partialBody = partialBody;
        }
    }
}
