# KeybindsGalore Plus

> [!WARNING]<br>
> Currently (20 June), this mod requires **JAVA 21** to run, because I accidentally compiled it with JDK21.<br>
> See the messages above and below [this one in the FabricMC discord](https://discord.com/channels/507304429255393322/507982463449169932/1253163544669720720) for more info.<Br>
> I'm fixing this right now, but it'll take a while :(

> [!NOTE]<br>
> This project is a fork of KeybindsGalore, originally by Cael and updated to 1.20 by HVB007.
> <br>[HVB007's project is here](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x), and Cael's [original project is here](https://github.com/CaelTheColher/KeybindsGalore).

<br>

This mod opens a pie menu when a key bound to multiple actions is pressed! No more weird behaviour on conflicting keys :D

<p align="center">
  <img src="https://github.com/AV306/MultiBind/blob/346c3698d849e3c044e2d02d8c012f0339a0e449/images/IMG_2907.jpeg" width="70%" />
</p>

<br>

## Modifications to Original

- Reduced the list of keys which will never open a pie menu (more details [here](docs.md))
- Slightly optimised conflict searching and rendering
- Keybind labels now show their category along with their name, for easier identification ([example](docs.md))
- Added a small region (deadzone) at the centre of the pie menu that will not activate any binding
- Label texts no longer run off the screen
  
<br>

## Coming soon

- Configuration system for pie menu radius, transparency, animation and color

<br>

## Big Thanks to

Racoocoo (on the FabricMC discord) for telling me about the Java 21 issue and pointing out that GitHub issues were disabled!

<br>

## Gallery

[TODO: GIF of pie menu opening, a sector being selected, and the action triggering]

[TODO: GIF of pie menu opening, cursor moving out of and back into the deadzone, and the pie menu closing without any action happening]

[TODO: GIF of pie menu opening, cursor moving through each sector in a clockwise manner, selecting one and closing the menu]

<br>
<br>

## [[ Old README below ]]

# KeybindsGalore_HVB007_1.20.x
Updated to 1.20 by HVB007.

>Github : https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x 
>Fabric mod Which opens an popup when there are multiple actions bound to the same key in the Minecraft>controls>Keybinds settings. then choose one of the options to use.

>Changelog keybindsgalore-0.2-1.20:

Works with 1.20.2

Added Feature: Will not open the menu when pressing certain keys (Due to keys compatibility with other mods) as follows: 
1.tab 
2.caps lock 
3.left shift 
4.left control 
5.space 
6.left alt 
7.w 
8.a 
9.s 
10.d

Future Feature: Add mod setting to configure the keys to disable.

>Does not support conflicting Keybinds not using the Minecraft Keybinds settings.

Updated to 1.20.x by HVB007

Updated version of keybindsgalore by Cael : https://github.com/CaelTheColher/KeybindsGalore
