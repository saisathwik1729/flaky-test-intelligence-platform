package com.ftip.ftip.statemachine;

public class InvalidStateTransitionException extends RuntimeException{
    public InvalidStateTransitionException(String message){
        super(message);
    }
}
