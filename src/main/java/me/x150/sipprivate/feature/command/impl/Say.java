/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;

import java.util.Objects;

public class Say extends Command {

    public Say() {
        super("Say", "Says something in chat", "say", "tell");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(message)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("not sure if i can say nothing");
            return;
        }
        Objects.requireNonNull(CoffeeClientMain.client.player).sendChatMessage(String.join(" ", args));
    }
}
