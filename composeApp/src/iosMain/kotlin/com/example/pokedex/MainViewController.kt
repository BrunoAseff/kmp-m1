package com.example.pokedex

import androidx.compose.ui.window.ComposeUIViewController
import androidx.room.Room
import com.example.pokedex.data.local.AppDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSSearchPathForDirectoriesInDomains

fun MainViewController() = ComposeUIViewController {
    App(
        databaseBuilderFactory = {
            Room.databaseBuilder<AppDatabase>(
                name = "${documentsDirectory()}/pokedex.db"
            )
        }
    )
}

private fun documentsDirectory(): String =
    NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
