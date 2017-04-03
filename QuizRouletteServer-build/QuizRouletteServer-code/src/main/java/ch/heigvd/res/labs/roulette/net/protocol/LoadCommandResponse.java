package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by Thibaud Besseau on 30-Mar-17.
 * This class is used to load student in the store
 * @author Thibaud Besseau
 * @author Nathalie Megevand
 */
public class LoadCommandResponse
{
    private String status;
    private int numberOfNewStudents;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getNumberOfNewStudents()
    {
        return numberOfNewStudents;
    }


    public void setNumberOfNewStudents(int numberOfNewStudents)
    {
        this.numberOfNewStudents = numberOfNewStudents;
    }

    public LoadCommandResponse(String status, int numberOfNewStudents)
    {
        this.numberOfNewStudents = numberOfNewStudents;
        this.status = status;
    }
}
