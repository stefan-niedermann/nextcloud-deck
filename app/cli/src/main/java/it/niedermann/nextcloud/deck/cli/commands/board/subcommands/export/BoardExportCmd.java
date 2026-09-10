package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export;

import picocli.CommandLine.Command;

@Command(name = "export",
        mixinStandardHelpOptions = true,
        description = "Export board",
        subcommands = {
                ExportCsvCmd.class,
                ExportMermaidCmd.class,
                ExportOdtCmd.class,
                ExportPdfCmd.class
        })
public class BoardExportCmd {
}
