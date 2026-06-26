package com.jetpackduba.gitnuro.terminal

import com.jetpackduba.gitnuro.domain.IShellManager
import javax.inject.Inject

class LinuxTerminalProvider @Inject constructor(
    private val shellManager: IShellManager,
) : ITerminalProvider {
    override fun getTerminalEmulators(): List<TerminalEmulator> {
        return listOf(
            TerminalEmulator("Ptyxis", "ptyxis"),
            TerminalEmulator("Gnome Terminal", "gnome-terminal"),
            TerminalEmulator("KDE Terminal", "konsole"),
            TerminalEmulator("XFCE Terminal", "xfce4-terminal"),
            TerminalEmulator("Mate Terminal", "mate-terminal"),
            TerminalEmulator("LXQT Terminal", "qterminal"),
            TerminalEmulator("Kitty", "kitty"),
            TerminalEmulator("Alacritty", "alacritty"),
            TerminalEmulator("WezTerm", "wezterm"),
            TerminalEmulator("Foot", "foot"),
        )
    }

    override fun isTerminalInstalled(terminalEmulator: TerminalEmulator): Boolean {
        val checkTerminalInstalled = shellManager.runCommand(listOf("which", terminalEmulator.path))

        return !checkTerminalInstalled.isNullOrEmpty()
    }

    override fun startTerminal(terminalEmulator: TerminalEmulator, repositoryPath: String) {
        shellManager.runCommandInPath(listOf(terminalEmulator.path), repositoryPath)
    }
}
