package eu.vre4eic.evre.telegram.commands;


import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * This commands delete the Telegram account as authenticator
 *
 */
public class RemoveAuthCommand extends BotCommand {

    public static final String LOGTAG = "STARTCOMMAND";

    public RemoveAuthCommand() {
        super("remove", "This command deletes this Telegram account as authenticator for <i>username</i> in e-VRE, usage: <i>/removea username password</i>");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        
    }
}