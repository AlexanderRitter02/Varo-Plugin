package de.alexanderritter.varo.api;

import org.bukkit.ChatColor;

import de.alexanderritter.varo.main.Varo;

public class VaroMessages {
	
	public static String playerNotRegistered(String player) {
		return ChatColor.RED + "Der Spieler " + ChatColor.GOLD + player + ChatColor.RED + " existiert nicht oder ist nicht im Varo registriert.";}
	
	public static String playerNotDead(String player) {
		return ChatColor.RED + "Der Spieler " + ChatColor.GOLD + player + ChatColor.RED + " ist nicht tot. Du kannst ihn nicht wiederbeleben.";}
	
	public static String reviveSuccessful(String player) {
		return ChatColor.GREEN + "Der Spieler " + ChatColor.GOLD + player + ChatColor.GREEN + " wurde erfolgreich wiederbelebt.";}
	
	
	
	// Spawn-related
	
	public static String spawnCreated(int id) {
		return Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich erstellt.";}
	
	public static String spawnOverwritten(int id) {
		return Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich überschrieben.";}
	
	public static String lobbySet(int x, int y, int z) {
		return Varo.prefix + ChatColor.DARK_GREEN + "Ein neuer Ort für die Lobby mit den Koordinaten " + ChatColor.GREEN + x + ", " + y + ", " + z + ChatColor.DARK_GREEN + " wurde erfolgreich gesetzt.";}
	
	public static String lobbyCanOnlyBeSetIngame = ChatColor.RED + "Die Koordinaten der Lobby müssen ingame von einem Spieler festgelegt werden";
	
	
	// Strikes
	
	public static String strikesEmpty(String player) {return Varo.prefix + ChatColor.RED + "Der Spieler " + player + " hat keine Strikes.";}
	
	public static String strikeRemoved(String player, String strike) {
		return Varo.prefix + ChatColor.GREEN + "Der Strike " + ChatColor.YELLOW + strike + ChatColor.GREEN + " wurde von " + player + " entfernt.";}
	
	
	
	// Teams
	
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
	public static String morePlayersThanSpawns = ChatColor.RED + "Es sind mehr Spieler vorhanden als es Spawns gibt!";
	public static String varoAlreadyRunning = ChatColor.RED + "Varo läuft bereits!";
	public static String varoBeingStopped = ChatColor.RED + "Varo wird beendet!";
	public static String varoStopped = Varo.prefix + ChatColor.RED + "Varo wurde von einem Admin beendet!";
	public static String varoNotYetStarted = ChatColor.RED + "Varo ist noch nicht gestartet. Benutze /varo.start, um Varo zu starten";
	
	
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
	
	// Spectators
	public static String spectateOnlyIfTeamDead = ChatColor.RED + "Du kannst nur spectaten, wenn dein ganzes Team gestorben ist.";
	public static String spectateOnlyIfMemberOnline = ChatColor.RED + "Du kannst nur spectaten, wenn ein Teammitglied online ist.";
	public static String kickedBcPlayerLeft(String player) {return ChatColor.RED + "Du wurdest gekickt, weil " + ChatColor.GOLD +  player +  ChatColor.RED + " das Spiel verlassen hat.";}
	public static String teleportedTo(String player) {return Varo.prefix + ChatColor.DARK_GREEN + "Du wurdest zu " + ChatColor.GREEN + player + ChatColor.DARK_GREEN + " teleportiert!";}
	public static String teleportToFailed(String player) {
		return Varo.prefix + ChatColor.DARK_RED + "Du konntest nicht teleportiert werden, " + ChatColor.RED + player + ChatColor.DARK_RED + " ist nicht mehr online.";}
	
	
	// Other
	public static String nointeger = ChatColor.RED + "Bitte gebe eine Integer an (positive ganze Zahl) an";
	
}
