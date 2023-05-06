import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Baba Puzzle Call Outs",
        description = "Call outs for Baba Puzzle Room in TOA",
        tags = {"TOA", "Baba Puzzle", "Call Outs"}
)
public class BabaPuzzleCalloutsPlugin extends Plugin {

    private static final int CORRUPTION_MARKED_WIDGET_ID = WidgetInfo.DIALOG_SPRITE_SPRITE_ID.getId();
    private static final int PILLAR_DAMAGED_WIDGET_ID = WidgetInfo.DIALOG_SPRITE_TEXT.getId();
    private static final int VENT_OVERLOADED_WIDGET_ID = WidgetInfo.DIALOG_SPRITE_TEXT.getId();
    private static final String STACK_MESSAGE = "STACK!";
    private static final String PILLAR_MESSAGE = "PILLAR!";
    private static final String VENT_MESSAGE = "VENT!";

    private Client client;

    public BabaPuzzleCalloutsPlugin(Client client) {
        this.client = client;
    }

    @Override
    protected void startUp() {
        // Subscribe to events
        clientThread.invokeLater(() -> {
            client.getEventBus().register(this);
        });
    }

    @Override
    protected void shutDown() {
        // Unsubscribe from events
        clientThread.invokeLater(() -> {
            client.getEventBus().unregister(this);
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            // Reset plugin state when logging in
            resetState();
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        int widgetId = event.getGroupId();

        switch (widgetId) {
            case WidgetID.BA_BABA_YAGA_GAME_GROUP_ID:
                // Baba Yaga's Puzzle widget group ID
                Widget corruptionMarkedWidget = client.getWidget(CORRUPTION_MARKED_WIDGET_ID);
                Widget pillarDamagedWidget = client.getWidget(PILLAR_DAMAGED_WIDGET_ID);
                Widget ventOverloadedWidget = client.getWidget(VENT_OVERLOADED_WIDGET_ID);

                if (corruptionMarkedWidget != null && corruptionMarkedWidget.isHidden()) {
                    // Corruption marked widget is hidden, meaning player is marked for corruption
                    sendMessage(ChatMessageType.GAMEMESSAGE, STACK_MESSAGE);
                } else if (pillarDamagedWidget != null && pillarDamagedWidget.getText().contains("Pillar")) {
                    // Pillar damaged widget text contains "Pillar", meaning a pillar needs to be repaired
                    sendMessage(ChatMessageType.GAMEMESSAGE, PILLAR_MESSAGE);
                } else if (ventOverloadedWidget != null && ventOverloadedWidget.getText().contains("vent")) {
                    // Vent overloaded widget text contains "vent", meaning the vent needs to be cleared
                    sendMessage(ChatMessageType.GAMEMESSAGE, VENT_MESSAGE);
                }
                break;
        }
    }

    private void resetState() {
        // Reset plugin state
    }

    private void sendMessage(ChatMessageType messageType, String message) {
        // Send a message to the in-game chat
        client.addChatMessage(messageType, "",
