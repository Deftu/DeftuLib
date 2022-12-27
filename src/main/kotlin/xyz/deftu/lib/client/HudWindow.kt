package xyz.deftu.lib.client

@Deprecated(
    message = "This class is only here temporarily for backwards compatibility.",
    replaceWith = ReplaceWith("xyz.deftu.lib.client.hud.HudWindow"),
    level = DeprecationLevel.WARNING
) class HudWindow : xyz.deftu.lib.client.hud.HudWindow()
