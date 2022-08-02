package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economycore.EconomyAPI;

public class EconomyCommand {

    private final Main main;

    public EconomyCommand(Main main) {
        this.main = main;
    }

    public void register() {
        new CommandTree("economy")
                .then(new LiteralArgument("offers")
                        .then(new LiteralArgument("buy")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("create")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("cancel")
                                .executesPlayer((player, args) -> {

                                })
                        )
                )
                .then(new LiteralArgument("coins")
                        .then(new LiteralArgument("give")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("take")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("set")
                                .executesPlayer((player, args) -> {

                                })
                        )
                )
                .then(new LiteralArgument("permission")
                        .then(new LiteralArgument("group")
                                .then(new LiteralArgument("set")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                        .then(new LiteralArgument("single")
                                .then(new LiteralArgument("set")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                )
                .then(new LiteralArgument("config")
                        .then(new LiteralArgument("startBalance")
                                .then(new DoubleArgument("startBalance")
                                        .executesPlayer((player, args) -> {
                                            double newStartBalance = (double) args[0];
                                            EconomyAPI.setStartBalance(newStartBalance);
                                        })
                                )
                        )
                        .then(new LiteralArgument("interest"))
                )
                .then(new LiteralArgument("trade")
                        .executesPlayer((player, args) -> {

                        })
                )
                .register();
    }
}
