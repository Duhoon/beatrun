package com.lastsemester.beatrun;

public class Judgement {

    final private int MAX_HBR = 220;
    final private int STABLE_HBR = 65;
    final private double LIGHT_FLOOR = 0.6;
    final private double MODERATE_FLOOR = 0.7;
    final private double HARD_FLOOR = 0.8;
    final private double HARD_CEIL = 0.9;

    private int weight;
    private int height;
    private int age;
    private int strength;

    public Judgement(){

    }

    public Judgement(int weight, int height, int age, int strength){
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.strength = strength;
    }

    public int getFloorHBR(){
        int floorHBR;
        int range = MAX_HBR - age;
        int margin = range - STABLE_HBR;

        if(strength == 1){
            floorHBR = (int) Math.ceil(margin * LIGHT_FLOOR) + STABLE_HBR;
        } else if(strength == 2){
            floorHBR = (int) Math.ceil(margin * MODERATE_FLOOR) + STABLE_HBR;
        } else{
            floorHBR = (int) Math.ceil(margin * HARD_FLOOR) + STABLE_HBR;
        }

        return floorHBR;
    }

    public int getCeilHBR(){
        int ceilHBR;
        int range = MAX_HBR - age;
        int margin = range - STABLE_HBR;

        if(strength == 1){
            ceilHBR = (int) Math.ceil(margin * MODERATE_FLOOR) + STABLE_HBR;
        } else if(strength == 2){
            ceilHBR = (int) Math.ceil(margin * HARD_FLOOR) + STABLE_HBR;
        } else{
            ceilHBR = (int) Math.ceil(margin * HARD_CEIL) + STABLE_HBR;
        }
        return ceilHBR;
    }
}
