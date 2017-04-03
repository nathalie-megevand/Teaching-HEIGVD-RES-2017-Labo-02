package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Thibaud Besseau on 30-Mar-17.
 * This class send all the informations about the session when
 * the client ask the end of the connexion
 * @author Thibaud Besseau
 * @author Nathalie Megevand
 */
public class ByeCommandReponse
{

    private String status;
    private int numberOfCommands;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getNumberOfCommands()
    {
        return numberOfCommands;
    }


    public ByeCommandReponse(String status, int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
        this.status = status;
    }

}
