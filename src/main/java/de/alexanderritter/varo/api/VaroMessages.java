package de.alexanderritter.varo.api;

import org.bukkit.ChatColor;

import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class VaroMessages {
	
	public static String playerNotRegistered(String player) {
		return ChatColor.RED + "Der Spieler " + ChatColor.GOLD + player + ChatColor.RED + " existiert nicht oder ist nicht im Varo registriert.";}
	
	public static String playerNotDead(String player) {
		return ChatColor.RED + "Der Spieler " + ChatColor.GOLD + player + ChatColor.RED + " ist nicht tot. Du kannst ihn nicht wiederbeleben.";}
	
	public static String reviveSuccessful(String player) {
		return ChatColor.GREEN + "Der Spieler " + ChatColor.GOLD + player + ChatColor.GREEN + " wurde erfolgreich wiederbelebt.";}
	public static String DISCORD_reviveSuccessfull(String player) {
		return "```css\n Der Spieler " + player + " wurde wiederbelebt. Viel Glück!.\n ```";
	}
	
	public static String playerDoesntExistOrServersDown(String player) {
		return ChatColor.RED + "Der Spieler " + player + " existiert nicht oder Mojangs Auth-Server sind down";
	}
	
	
	// Spawn-related
	
	public static String spawnCreated(int id) {
		return Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich erstellt.";}
	
	public static String spawnOverwritten(int id) {
		return Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich überschrieben.";}
	
	public static String lobbySet(int x, int y, int z) {
		return Varo.prefix + ChatColor.DARK_GREEN + "Ein neuer Ort für die Lobby mit den Koordinaten " + ChatColor.GREEN + x + ", " + y + ", " + z + ChatColor.DARK_GREEN + " wurde erfolgreich gesetzt.";}
	
	public static String lobbyCanOnlyBeSetIngame = ChatColor.RED + "Die Koordinaten der Lobby müssen ingame von einem Spieler festgelegt werden";
	public static String spawnIdMinOne = ChatColor.RED + "Die ID muss mindestens 1 betragen.";
	public static String spawnIdinOrder = ChatColor.RED + "Benutze die ID's in Reihenfolge! (1, 2, 3...)";
	public static String spawnsAreRegisterdIngame = ChatColor.RED + "Spawns werden ingame registriert";
	
	
	// Strikes
	
	public static String strikesEmpty(String player) {return Varo.prefix + ChatColor.RED + "Der Spieler " + player + " hat keine Strikes.";}
	
	public static String strikeRemoved(String player, String strike) {
		return Varo.prefix + ChatColor.GREEN + "Der Strike " + ChatColor.YELLOW + strike + ChatColor.GREEN + " wurde von " + player + " entfernt.";}
	
	
	
	// Teams
	public static String registering = ChatColor.GREEN + "Registrierung läuft...";
	public static String teamDeleted(String team) {
		return ChatColor.GREEN + "Das Team " + ChatColor.GOLD + team + ChatColor.GREEN + " wurde erfolgreich gelöscht.";}
	
	public static String teamDoesntExist(String team) {
		return ChatColor.RED + "Das Team " + ChatColor.GOLD + team + ChatColor.RED + " existiert nicht.";}
	
	public static String teamAlreadyRegistered = ChatColor.RED + "Das Team ist schon registriert";
	
	public static String teamRegisteringFailed(String team) {
		return ChatColor.RED + "Registration von Team " + team + " fehlgschlagen!";}
	
	public static String teamRegisteredSuccessfully(String team) {
		return ChatColor.GREEN + "Team " + team + " wurde erfolgreich registriert";}
	
	public static String playerAlreadyRegisteredInAnotherTeam(String player) {
		return ChatColor.RED + "Der Spieler " + player + " ist schon in einem anderen Team registriert!";}
	
	
	// Countdown and Start
	public static String varoStartsIn(int seconds) {return Varo.prefix + ChatColor.GREEN + "Varo startet in " + seconds + " Sekunden, viel Erfolg an alle Teams!";}
	public static String DISCORD_varoStartsIn(int seconds) {return "```css\n+ Varo startet in " + seconds + " Sekunden, viel Erfolg an alle Teams!\n```";}
	public static String morePlayersThanSpawns = ChatColor.RED + "Es sind mehr Spieler vorhanden als es Spawns gibt!";
	public static String varoAlreadyRunning = ChatColor.RED + "Varo läuft bereits!";
	public static String varoBeingStopped = ChatColor.RED + "Varo wird beendet!";
	public static String varoStopped = Varo.prefix + ChatColor.RED + "Varo wurde von einem Admin beendet!";
	public static String DISCORD_varoStopped = "```diff\n- Varo wurde von einem Admin beendet!\n```";
	public static String varoNotYetStarted = ChatColor.RED + "Varo ist noch nicht gestartet. Benutze /varo.start, um Varo zu starten";
	public static String countdownNotRunningYet = ChatColor.RED + "Der Countdown läuft noch nicht.";
	public static String countdownAlreadyRunningUseBreakToCancel = ChatColor.RED + "Der Countdown läuft schon. Benutze /varo.start break, um ihn zu stoppen";
	public static String spawnsNotProperlySetUp = ChatColor.RED + "Es ist noch kein Spawn registriert / die Spawns sind nicht in der richtigen Reihenfolge. Bitte benutze /varo.spawn";
	
	// Sieg oder Ende
	public static String wonTheGame(VaroPlayer winner) {
		return Varo.prefix + winner.getColor() + winner.getName() + " hat das Varo gewonnen (#" + winner.getTeam() + "). Gratuliere!";
	}
	public static String DISCORD_wonTheGame(String player) {
		return player + " hat das Varo gewonnen!";
	}
	public static String DISCORD_winPartyMessage = ":medal: Ruhm und Ehre, ein Hoch auf den Sieg! :partying_face:";
	
	// Schutzzeit
	public static String protectionTimeEndsInMinutes(int minutes) {
		return Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + minutes + ChatColor.RED + " Minuten zu Ende";
	}
	public static String protectionTimeEndsInSeconds(int seconds) {
		if(seconds == 0) {
			return Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + "1" + ChatColor.RED + " Sekunde zu Ende";
		} else {
			return Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + seconds + ChatColor.RED + " Sekunden zu Ende";
		}
	}
	public static String protectionTimeExipred = Varo.prefix + ChatColor.GREEN + "Die Schutzzeit ist zu Ende. Ihr könnt euch nun angreifen!";
	public static String protectionTimeExpired_Actionbar = ChatColor.RED + "Die Schutzzeit ist zu Ende. Let's go!";
	
	// Teamchests
	public static String yes = ChatColor.GREEN + "Ja";
	public static String no = ChatColor.DARK_RED + "Nein";
	public static String chestAlreadyExistsCreateNewOne = Varo.prefix + ChatColor.RED + "Du hast schon eine Teamchest. Soll eine neue erstellt werden?";
	public static String chestCreated = Varo.prefix + ChatColor.GREEN + "Teamkiste erstellt";
	public static String chestDestroyed = Varo.prefix + ChatColor.RED + "Du hast deine Teamchest zerstört";
	public static String chestOverwritten = Varo.prefix + ChatColor.DARK_GREEN + "Deine Teamchest wurde erfolgreich überschrieben!";
	public static String chestDoesntExistAnymore = ChatColor.RED + "Die Teamchest, die du vorgeschlagen hast, existiert nicht mehr";
	public static String chestSignDoesntExist = ChatColor.RED + "An der vorgeschlagenen Stelle gibt es kein Schild mehr";
	public static String noChestRequests = ChatColor.RED + "Du hast zurzeit keine Teamchest-Anfragen";
	public static String doubleChestMissing = ChatColor.RED + "An der vorgeschlagenen Stelle gibt es keine Doppelkiste";
	public static String chestOwnedBy(String team) {
		return ChatColor.RED + "Diese Truhe gehört Team " + ChatColor.GOLD + "#" + team;}
	
	// Sessions
	public static String noSessionsThisWeek = ChatColor.RED + "Du hast keine Sessions mehr in dieser Woche!";
	public static String maxSessionsPerDay(int max) {return ChatColor.RED + "Du kannst maximal " + max + " Sessions pro Tag spielen!";}
	
	
	// Events
	public static String cannotCraftItem = ChatColor.RED + "Du darfst dieses Item nicht craften!";
	public static String cannotUseItem = ChatColor.RED + "Du darfst dieses Item nicht benutzen!";
	public static String potionsNotAllowed = ChatColor.RED + "Ein Braustand in der Nähe hat versucht, einen Trank zu brauen. Tränke sind nicht erlaubt!";
	public static String potionIllegal = ChatColor.RED + "Ein Braustand in der Nähe hat versucht, einen illegalen Trank zu brauen!";
	public static String DISCORD_playerDied(String player) {return "```http\n " + player + " ist aus Varo ausgeschieden.\n ```";}
	public static String deathTITLE = "Du bist gestorben";
	public static String deathSUBTITLE = "Somit bist du aus Varo ausgeschieden";
	public static String youDied = ChatColor.RED + "Du bist gestorben!\n" + "Somit bist du aus Varo ausgeschieden.\n";
	
	// Spectators
	public static String spectateOnlyIfTeamDead = ChatColor.RED + "Du kannst nur spectaten, wenn dein ganzes Team gestorben ist.";
	public static String spectateOnlyIfMemberOnline = ChatColor.RED + "Du kannst nur spectaten, wenn ein Teammitglied online ist.";
	public static String kickedMemberLeft(String player) {return ChatColor.RED + "Du wurdest gekickt, weil " + ChatColor.GOLD +  player +  ChatColor.RED + " das Spiel verlassen hat.";}
	public static String teleportedTo(String player) {return Varo.prefix + ChatColor.DARK_GREEN + "Du wurdest zu " + ChatColor.GREEN + player + ChatColor.DARK_GREEN + " teleportiert!";}
	public static String teleportToFailed(String player) {
		return Varo.prefix + ChatColor.DARK_RED + "Du konntest nicht teleportiert werden, " + ChatColor.RED + player + ChatColor.DARK_RED + " ist nicht mehr online.";}
	public static String spectatorsAre = "Spectators are: ";
	
	
	// Admins
	public static String temporaryAdmin(String player) {return ChatColor.GREEN + player + " ist nun temporär Admin";}
	public static String temporaryAdminKickNotice = ChatColor.DARK_GREEN + "Für die nächste Session bist du temporär Admin. \nVerwende das nur, um etwas zu fixen und logge dich danach wieder aus";
	public static String adminForever(String player) {return ChatColor.GREEN + player + " ist nun " + ChatColor.DARK_RED + "für immer " + ChatColor.GREEN + "Admin";}
	public static String adminForeverKickNotice = ChatColor.DARK_RED + "Du bist nun Admin und überwachst das Spiel. Danke! \n" + ChatColor.RED + "Melde das sofort, falls du am Varo teilnehmen willst.";
	public static String adminModeTITLE = "Du bist nun im Admin Modus";
	public static String adminModeSUBTITLE = "Nicht zum eigenen Vorteil verwenden";
	
	
	// /modifyconfig
	public static String changedConfig(String attribute, String value) {
		return "Das Attribut '" + attribute + "' wurde auf den Wert '" + value + "' gesetzt.";
	}
	public static String noConfigEntry(String attribute) {
		return "Es liegt kein Eintrag mit dem Attribut '" + attribute + "' in der config.yml vor";
	}
	
	// Strikes
	public static String strikeIndexMin1 = ChatColor.RED + "Der Index muss mindestens 1 betragen.";
	public static String strikeReceived(String player, String reason) {
		return Varo.prefix + ChatColor.RED + player + " hat einen Strike erhaten für: " + ChatColor.YELLOW + reason;
	}
	public static String strikeNotFound(String player, int index) {
		return ChatColor.RED + "Der Spieler " + player + " besitzt keinen Strike mit Index " + index;
	}
	public static String playerHasTheFollowingStrikes(String player) {
		return Varo.prefix + ChatColor.GREEN + "Der Spieler " + player + " hat folgende Strikes: ";
	}
	public static String DISCORD_strikeReceived(String player) {
		return ":warning: " + player + " hat einen Strike erhalten";
	}
	
	// Other
	public static String nointeger = ChatColor.RED + "Bitte gebe eine Integer an (positive ganze Zahl) an";
	public static String noPermissionToPostCoordinates = ChatColor.RED + "Du hast nicht die Berechtigung, für andere Spieler Koordinaten zu posten.";
	public static String playerSettingsOnlyChangableAfterStart = ChatColor.RED + "Spieler-Einstellungen lassen sich erst nach dem Start des Varos ändern.";
	
	public static String reloadedPlayerYML = ChatColor.GREEN + "players.yml erfolgreich neu geladen.";
	public static String kickBecauseNotRegistered = ChatColor.RED + "Du bist nicht registriert.\nBitte informiere den Admin, falls du glaubst, dies sei ein Fehler!";
	public static String outsideBorder = ChatColor.RED + " [WARNING] Du bist außerhalb der Worldborder";
	public static String chatDisabled = ChatColor.RED + "Du kannst jetzt nicht chatten";
	public static String commandCanOnlyBeUsedIngame = ChatColor.RED + "Dieser Befehl kann nur ingame (als Spieler) ausgeführt werden.";
	
	public static String spectatorPrefix = "[Spectator]";
	public static String sessions = "Sessions";
	
}
