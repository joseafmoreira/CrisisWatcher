package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.io.IOSharedResources;

public class StandardInput extends Thread {
    private static final String INVALID_COMMAND_MESSAGE = "O comando é inválido\nDigite /help para obter a lista de comandos disponíveis";
    private static final List<List<Object>> COMMAND_LIST = List.of(
        List.of(CommandLevel.NOAUTH, "/register <username> <password> <profile> - Regista um utilizador"),
        List.of(CommandLevel.NOAUTH, "/login <username> <password> - Autentica um utilizador"),
        List.of(CommandLevel.AUTH, "/info - Apresenta as informações relacionadas com o utilizador"),
        List.of(CommandLevel.AUTH, "/username <new_username> - Altera o nome de utilizador"),
        List.of(CommandLevel.AUTH, "/password <new_password> - Altera a palavra-passe do utilizador"),
        List.of(CommandLevel.AUTH, "/logout - Desconecta o utilizador"),
        List.of(CommandLevel.ALL, "/createRoom <name> - Cria uma sala de chat"),
        List.of(CommandLevel.ALL, "/help - Apresenta uma lista dos comandos disponíveis ao cliente"),
        List.of(CommandLevel.ALL, "/close - Fecha a aplicação")
    );
    private UserDTO userDTO;
    private List<String> tcpOutputBuffer;
    private List<String> udpOutputBuffer;
    private BufferedReader standardInput;

    public StandardInput(IOSharedResources ioSharedResources) {
        userDTO = ioSharedResources.getUserDTO();
        tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
        udpOutputBuffer = ioSharedResources.getUdpOutputBuffer();
        standardInput = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
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
                } else {
                    udpOutputBuffer.add(input);
                }
            }
        } catch (IOException ignored) {
            interrupt();
        }
    }

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

    private enum CommandLevel {
        AUTH,
        NOAUTH,
        ALL
    }
}
