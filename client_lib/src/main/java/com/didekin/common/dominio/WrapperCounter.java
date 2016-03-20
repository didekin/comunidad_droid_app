package com.didekin.common.dominio;

/**
 * User: pedro@didekin
 * Date: 18/03/16
 * Time: 19:57
 */
@SuppressWarnings("unused")
public class WrapperCounter {

    long counter;

    public WrapperCounter(long counter)
    {
        this.counter = counter;
    }

    public long getCounter()
    {
        return counter;
    }

    public void addCounter(){
        ++counter;
    }

    public void addCounter(int howMuch){
        counter += howMuch;
    }
}
