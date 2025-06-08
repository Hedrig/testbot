package com.example;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot  {

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update arg0) 
    {
        Long uid = arg0.getMessage().getFrom().getId();
        if (!arg0.getMessage().isCommand()){
            System.out.println(arg0.getMessage());
            sendText(uid, arg0.getMessage().getText());
            return;
        }
        switch (arg0.getMessage().getText()) {
            case "/test":
                sendText(uid, "Test");
                break;
            case "/restart":
                sendText(uid, "Bot restarted");
                break;
            case "/cat":
                sendSticker(uid, System.getenv("CAT_ID"));
                break;
            case "/randomcat":
                sendRandomSticker(uid);
                break;
        }
    }

    void sendText(Long who, String what)
    {
        SendMessage sm = SendMessage.builder()
        .chatId(who.toString())
        .text(what)
        .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    void sendSticker(Long who, String what)
    {
        SendSticker ss = SendSticker.builder()
            .chatId(who.toString())
            .sticker(new InputFile(what))
            .build();
        try {
            execute(ss);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    void sendSticker(Long who, Sticker what)
    {
        sendSticker(who, what.getFileId());
    }

    void sendRandomSticker(Long who)
    {
        List<Sticker> stickers = getStickersSet();
        int size = stickers.size();
        int randomIndex = (int)(Math.random() * size);
        sendSticker(who, stickers.get(randomIndex));
    }

    List<Sticker> getStickersSet()
    {
        // КОТИКИ
        GetStickerSet gss = GetStickerSet.builder()
        .name(System.getenv("CAT_SET_NAME"))
        .build();
        try {
            return execute(gss).getStickers();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

}
