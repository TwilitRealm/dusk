#pragma once

namespace dusk::mods::svc {

// Marks the mods' config keys dirty for the config service's debounced save. The loader
// calls this for the enabled cvars it owns.
void config_mark_dirty();

}  // namespace dusk::mods::svc
