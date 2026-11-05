
package com.languagescape;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
		name = "LanguageScape"
)
public class LanguageScapePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LanguageScapeConfig config;

	@Inject
	private ItemManager itemManager;

	// Stores the quantities from the previous inventory update.
	private Map<Integer, Integer> previousInventory = null;

	@Override
	protected void startUp()
	{
		previousInventory = null;
		log.debug("LanguageScape started!");
	}

	@Override
	protected void shutDown()
	{
		previousInventory = null;
		log.debug("LanguageScape stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			previousInventory = null;
		}

		if (event.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(
					ChatMessageType.GAMEMESSAGE,
					"",
					"LanguageScape is ready!",
					null
			);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		// Only monitor the player's inventory.
		if (event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		// Count how many of each item we currently have.
		Map<Integer, Integer> currentInventory = new HashMap<>();

		for (Item item : event.getItemContainer().getItems())
		{
			if (item.getId() == -1)
			{
				continue;
			}

			currentInventory.merge(
					item.getId(),
					item.getQuantity(),
					Integer::sum
			);
		}

		// Compare against the previous inventory snapshot.
		if (previousInventory != null
				&& client.getGameState() == GameState.LOGGED_IN)
		{
			for (Map.Entry<Integer, Integer> entry
					: currentInventory.entrySet())
			{
				int itemId = entry.getKey();
				int currentQuantity = entry.getValue();

				int previousQuantity =
						previousInventory.getOrDefault(itemId, 0);

				int gained = currentQuantity - previousQuantity;

				if (gained > 0)
				{
					String itemName = itemManager
							.getItemComposition(itemId)
							.getName();

					client.addChatMessage(
							ChatMessageType.GAMEMESSAGE,
							"",
							"LanguageScape detected: "
									+ gained + " x " + itemName,
							null
					);
				}
			}
		}

		// Save the snapshot for the next comparison.
		previousInventory = currentInventory;
	}

	@Provides
	LanguageScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LanguageScapeConfig.class);
	}
}
