package ai.rever.boss.plugin.dynamic.rolecreation

import ai.rever.boss.plugin.api.Panel.Companion.right
import ai.rever.boss.plugin.api.Panel.Companion.top
import ai.rever.boss.plugin.api.Panel.Companion.bottom
import ai.rever.boss.plugin.api.PanelId
import ai.rever.boss.plugin.api.PanelInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle

object RoleCreationInfo : PanelInfo {
    override val id = PanelId("role-creation", 23)
    override val displayName = "Role Creation"
    override val icon = Icons.Outlined.AddCircle
    override val defaultSlotPosition = right.top.bottom
}
