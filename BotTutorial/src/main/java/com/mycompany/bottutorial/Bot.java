/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bottutorial;

import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author gganuza
 */
public class Bot extends TelegramLongPollingBot {

    private boolean screaming = false;

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;

    InlineKeyboardButton next = InlineKeyboardButton.builder()
            .text("Next").callbackData("next")
            .build();

    InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Back").callbackData("back")
            .build();

    InlineKeyboardButton url = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://core.telegram.org/bots/api")
            .build();

    @Override
    public String getBotUsername() {
        return "TutorialBot";
    }

    @Override
    public String getBotToken() {
        return "6417236345:AAG4FOoDbGNAODqEP8NAFxeJnAS5LmzEOTY";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // System.out.println("Recibiste un mensaje:\n" + update.getUpdateId().toString() + "-" + update.getMessage().getChatId()
        // + ": " + update.getMessage().getText());
        // System.out.println(update);
        String respuesta = "";
        long chatId = 0L;
        chatId = update.getMessage().getChatId();
        System.out.println(update.getMessage().getText().toLowerCase().replace(" ", ""));

        if (update.getMessage().isCommand()) {
            if (update.getMessage().getText().equals("/scream")) //If the command was /scream, we switch gears
            {
                screaming = true;
            } else if (update.getMessage().getText().equals("/whisper")) //Otherwise, we return to normal
            {
                screaming = false;
            }

            return;                                     //We don't want to echo commands, so we exit
        }

        switch (update.getMessage().getText().toLowerCase().replace(" ", "")) {
            case "hola":
                respuesta = "Hola, que tal " + update.getMessage().getFrom().getFirstName();
                break;
            case "comotellamas":
                respuesta = "Mi nombre es GG";
                break;
            default:
                //respuesta = "No te entiendo";
                /*respuesta = update.toString();
                System.out.println(update);*/
                copyMessage(chatId, update.getMessage().getMessageId());
                break;

        }
        if (screaming) //If we are screaming
        {
            update.getMessage().setText(respuesta);
            scream(chatId, update.getMessage());     //Call a custom method
        } else {
            sendText(chatId, respuesta);
        }

        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next)).build();

        //Buttons are wrapped in lists since each keyboard is a set of button rows
        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back))
                .keyboardRow(List.of(url))
                .build();

    }

    private void scream(Long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase());
        } else {
            copyMessage(id, msg.getMessageId());  //We can't really scream a sticker
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString()) //We copy from the user
                .chatId(who.toString()) //And send it back to him
                .messageId(msgId) //Specifying what message
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
