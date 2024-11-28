package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.io.IOSharedResources;

/**
 * Handles the user input from the standard input in a separate thread. <p>
 * 
 * The available constructors for this {@code StandardInput} include: <p>
 * <ul>
 *  <li>{@link #StandardInput(IOSharedResources)}: Constructs a new StandardInput object with a specified ioSharedResources</li>
 * </ul> 
 * 
 * The operations available for this {@code StandardInput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Starts listening to user input from the standard input and process the given user input</li>
 *  <li>{@link #getAvailableCommands(CommandLevel[])}: Returns the available command to be displayed when entering the /help command</li>
 * </ul> 
 * 
 * <h3>StandardInput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class StandardInput extends Thread {
    /**
     * Message displayed when an invalid command is entered
     */
    private static final String INVALID_COMMAND_MESSAGE = "O comando é inválido\nDigite /help para obter a lista de comandos disponíveis";
    /**
     * List of all available commands, categorized by their level and description. <p>
     * 
     * Each command is represented as a list containing:
     * <ul>
     *   <li>{@link CommandLevel} - The required level to execute the command.</li>
     *   <li>{@code String} - A description of the command.</li>
     * </ul>
     */
    private static final List<List<Object>> COMMAND_LIST = List.of(
        List.of(CommandLevel.NOAUTH, "/register <username> <password> <profile> - Regista um utilizador"),
        List.of(CommandLevel.NOAUTH, "/login <username> <password> - Autentica um utilizador"),
        List.of(CommandLevel.AUTH, "/info - Apresenta as informações relacionadas com o utilizador"),
        List.of(CommandLevel.AUTH, "/username <new_username> - Altera o nome de utilizador"),
        List.of(CommandLevel.AUTH, "/password <new_password> - Altera a palavra-passe do utilizador"),
        List.of(CommandLevel.AUTH, "/logout - Desconecta o utilizador"),
        List.of(CommandLevel.AUTH, "/createRoom <name> - Cria uma sala de chat"),
        List.of(CommandLevel.ALL, "/help - Apresenta uma lista dos comandos disponíveis ao cliente"),
        List.of(CommandLevel.ALL, "/close - Fecha a aplicação")
    );
    /**
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The TCP output buffer
     */
    private List<String> tcpOutputBuffer;
    /**
     * The UDP output buffer
     */
    private List<String> udpOutputBuffer;
    /**
     * The standard input buffered reader
     */
    private BufferedReader standardInput;

    /**
     * Constructs a new StandardInput object with a specified ioSharedResources.
     * 
     * @param ioSharedResources the specified ioSharedResources
     */
    public StandardInput(IOSharedResources ioSharedResources) {
        userDTO = ioSharedResources.getUserDTO();
        tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
        udpOutputBuffer = ioSharedResources.getUdpOutputBuffer();
        standardInput = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Starts listening to user input from the standard input and process the given user input.
     */
    @Override
    public void run() {

        System.out.println("Bem-vindo ao CrisisWatcher!\nCaso seja necessário, digite /help para obter a lista de comandos disponíveis");

        String input;
        try {
            while ((input = standardInput.readLine()) != null) {
                if (input.startsWith("/")) {
                    if (input.equals("/info")) {
                        System.out.println((userDTO.isLogged()) ? userDTO : INVALID_COMMAND_MESSAGE);
                        continue;
                    } else if (input.equals("/help")) {
                        System.out.println(getAvailableCommands(new CommandLevel[]{CommandLevel.ALL, (userDTO.isLogged()) ? CommandLevel.AUTH : CommandLevel.NOAUTH}));
                        continue;
                    }
                    tcpOutputBuffer.add(input);
                } else {
                    udpOutputBuffer.add(input);
                }
            }
        } catch (IOException ignored) {
            interrupt();
        }
    }

    /**
     * Returns the available command to be displayed when entering the /help command.
     * 
     * @param levels the levels that this user has.
     * @return a string representation of the available commands to the user
     */
    private String getAvailableCommands(CommandLevel[] levels) {
        String result = "Lista de comandos disponíveis\n";
        for (List<Object> command : COMMAND_LIST) {
            CommandLevel commandLevel = (CommandLevel) command.get(0);
            String commandMessage = (String) command.get(1);
            boolean authorized = false;
            for (CommandLevel level : levels) {
                if (level.equals(commandLevel)) {
                    authorized = true;
                    break;
                }
            }
            if (authorized) result += commandMessage + "\n";
        }

        return result.substring(0, result.length() - 1);
    }

    /**
     * Represents the possible level that a command can have.
     */
    private enum CommandLevel {
        /**
         * Represents a command that can only be executed when a user is authenticated
         */
        AUTH,
        /**
         * Represents a command that can only be executed when a user is not authenticated
         */
        NOAUTH,
        /**
         * Represents a command that can be executed in all circunstances
         */
        ALL
    }
}
