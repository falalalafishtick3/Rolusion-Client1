# Rolusion Client (Fabric, Minecraft 1.21.11)

Client-side utility mod with a category-panel Click GUI (Right Shift).

Movement: Sprint, ToggleSneak
Render:   Fullbright, Zoom (FOV slider), NoBob, CleanView, Crosshair (size/gap/thickness),
          FpsBoost (fast graphics, less particles/shadows, shorter distances, unfocused FPS cap)
World:    Waypoints (B saves your position; 5 nearest shown with distance)
HUD:      InfoHud, ArrayList, Keystrokes (with CPS), ArmorHud, Clock, PingHud, PotionHud,
          TargetInfo, Colors (GUI/crosshair accent hue)

GUI: left click toggles, right click opens settings (or collapses a header),
drag headers to move. Module states and settings are saved to config/rolusion.json.
Every module has a rebindable key in Controls (most are unbound by default).

## Build
1. Install JDK 21 and a recent Gradle (or open the folder in IntelliJ IDEA).
2. Run `gradle wrapper --gradle-version 9.2.0` once to create the wrapper.
3. `./gradlew runClient` to test, `./gradlew build` for build/libs/rolusion-client-1.0.0.jar.
4. Put the jar plus Fabric API in .minecraft/mods.

If Gradle can't resolve a version, copy the current values for 1.21.11 from
https://fabricmc.net/develop into gradle.properties and build.gradle.

## Adding a module
Extend Module, pick a Category, add it in ModuleManager.init().
