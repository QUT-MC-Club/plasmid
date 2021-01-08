package xyz.nucleoid.plasmid.error;

import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.config.PlasmidConfig;
import xyz.nucleoid.plasmid.game.ConfiguredGame;

public interface ErrorReporter extends AutoCloseable {
    ErrorReporter VOID = new ErrorReporter() {
        @Override
        public void report(Throwable throwable, @Nullable String context) {
        }

        @Override
        public void close() {
        }
    };

    static ErrorReporter open(String source) {
        PlasmidConfig config = PlasmidConfig.get();

        String webhookUrl = config.getErrorReportingWebhookUrl();
        if (webhookUrl == null) {
            return VOID;
        }

        return new DiscordErrorReporter(source, DiscordWebhook.open(webhookUrl));
    }

    // TODO: Find a way to get the config ID here.
    static ErrorReporter open(ConfiguredGame<?> game) {
        String source = game.getOptionalName().orElse("<unnamed>") + " (" + game.getType().getIdentifier() + ")";
        return ErrorReporter.open(source);
    }

    default void report(Throwable throwable) {
        this.report(throwable, null);
    }

    void report(Throwable throwable, @Nullable String context);

    @Override
    void close();
}
