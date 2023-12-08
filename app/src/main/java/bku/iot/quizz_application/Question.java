package bku.iot.quizz_application;

import java.io.Serializable;

public class Question implements Serializable {

    private String question;

    private String backgroundColor;

    public Question(String question, String backgroundColor) {
        this.question = question;
        this.backgroundColor = backgroundColor;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getColor()
    {
        return backgroundColor;
    }

    public void setColor(String backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }
}
