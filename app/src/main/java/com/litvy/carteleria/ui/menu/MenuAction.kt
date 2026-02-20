package com.litvy.carteleria.ui.menu

sealed class MenuAction{
    object ChangeAnimation : MenuAction()
    object ChangeSpeed : MenuAction()
    object Restart : MenuAction()
}


