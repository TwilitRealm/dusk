**Kamilink's Lazy Tweaks**

A custom build of TwilitRealm's PC port of The Legend of Zelda: Twilight Princess, Dusklight. Tested on Fedora Linux (KDE Plasma), Windows 11, iOS, and Android.

**Demo Video (Youtube):**

[![Demo Video](https://img.youtube.com/vi/hzjTxhaUWG4/0.jpg)](https://www.youtube.com/watch?v=hzjTxhaUWG4)

This was mainly a fun side project for me as someone who is pretty new to C++ and was, until about two weeks ago, completely unfamiliar with the decomp code. I started this intending just to see what I could do and how much I could do on my own, but I also saw a lot of people in the Dusklight discord asking for some of these features, so I decided to release it as a mod/custom build.

Main features:
- Option in Dusklight menu difficulty section that allows you to control how much damage Link's sword can do

- Option in Dusklight gameplay menu that will keep the sword and shield visible on Wolf Link's back
  
  >_This will inevitably cause clipping with Midna's model. Will work on an option to toggle her visibility on/off soon._

- Manual shielding (no toggle option yet)
  
  >_Shield attack is now R+B, text changes to reflect this coming soon_

- Crouch shielding (no toggle option yet)
  
  >_Restored an unused animation, applies when holding R and not targeting (same as older games)_

- Extended Wolf Link finishers/Ending Blow

  >_You can now perform ending blow on most common enemies as Wolf Link!_

- Restart from the beginning of an area/dungeon upon death rather than beginning of a room (no toggle option yet)
  
  >_Older games in the series already did this, which I thought would be an appropriate change given TP's already piss easy difficulty_

- Lose rupees on death
  
  >_Optional toggle under difficulty section, lose half of Link's rupees on death (except some events, for now)_

- sudafed's No Hitstop toggle

  >_Optional toggle under gameplay section, turn off the brief frame pause when Link lands an attack_

Known issues:
- After performing a finisher/ending blow on an enemy as Wolf Link, you may be unable to jump attack until you perform a regular B attack or other action
  >_UPDATE: This has been fixed and will be included in the next update._

Planned features:
- Toggle options for features that do not currently have them
  
  >_Just makes sense to include this with the sword+shield visibility option._

- Z Button Items (Almost done)

  >_The QOL feature that everybody asks for. This is what is taking the most time and attention right now because Nintendo devs were smoking crack when they made this game._

- Whatever else I think of on a whim

**INSTALLATION:**

Windows: extract into its own folder and launch. If using the dusklight portable version, extract into your dusklight data folder.

Linux: extract and launch the appimage from anywhere not protected.

Android: Download, install, and launch.

iOS: Sideload to iOS using iloader or something similar. For more detailed instructions, see Dusklight's official github.

Mac: Not sure, don't have a Mac to test on. Extract and launch probably?

#
**ORIGINAL DUSKLIGHT README:**
<div align="center">
  <img src="res/logo.png" alt="Logo" width="640">

  <p align="center">
    <a href="https://twilitrealm.dev">Official Website</a>
    •
    <a href="https://discord.gg/6NpMhefCK9">Discord</a>
  </p>
</div>

# Overview

Dusklight is a reverse-engineered reimplementation of Twilight Princess.

It aims to be as accurate as possible to the original while also providing new options, enhancements, and tools to customize your experience.

# Setup

> [!IMPORTANT]
> Dusklight does *not* provide any copyrighted assets. You must provide your own copy of the original game.

> [!IMPORTANT]
> At a minimum, Dusklight requires a GPU with support for either D3D12, Vulkan, or Metal. Your experience with specific hardware, operating systems, and drivers may vary. In particular, older Intel iGPUs have a high likelihood of incompatibility. We are also aware of a number of issues on devices with Adreno GPUs and are working to resolve them.

### 1. Dump your game

You must dump your own copy of the game, please see [this article](https://wiki.dolphin-emu.org/index.php?title=Ripping_Games) for instructions. After dumping, you can use a program like [Dolphin](https://dolphin-emu.org/) or [nodtool](https://github.com/encounter/nod/releases) to convert the `.iso` to a `.rvz` to save space.

Currently, only the GameCube USA and EUR releases are supported. Support for other versions of the game is planned in the future.

### 2. Download [Dusklight](https://github.com/TwilitRealm/dusklight/releases)

### 3. Setup the game
**Windows / macOS / Linux**
- Extract the .zip file
- Launch Dusklight
- Press **Select Disc Image** and provide the path to your supported game dump
- Press **Play**!

**iOS**
- Follow the [iOS setup guide](docs/ios-install-altstore.md)

**Android**
- Install the Dusklight APK
- Launch Dusklight
- Press **Select Disc Image** and provide the path to your supported game dump
- Press **Play**!

# Building

If you'd like to build Dusklight from source, please read the [build instructions](docs/building.md).

Pull requests are welcomed! Note that we do not accept contributions that are primarily AI-generated and will close your PR if we suspect as much. Please also see the [code conventions](docs/code-conventions.md).

# Credits

Special thanks to the [TP decompilation](https://github.com/zeldaret/tp) team, the GC/Wii decompilation community, the [Aurora](https://github.com/encounter/aurora) developers, the [TP speedrunning community](https://zsrtp.link), and all [contributors](https://github.com/TwilitRealm/dusklight/graphs/contributors).

<br/>
<div align="center">
    <a href="https://github.com/encounter/aurora">
        <img src="assets/aurora-powered.png" alt="Powered by Aurora" width="800">
    </a>
</div>
