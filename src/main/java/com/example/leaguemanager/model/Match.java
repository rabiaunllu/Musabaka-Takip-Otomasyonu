package com.example.leaguemanager.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Match implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Team homeTeam;
    private Team awayTeam;
    private int homeScore;
    private int awayScore;
    private LocalDateTime matchDate;
    private boolean isPlayed;
    private int week;

    public Match(Team homeTeam, Team awayTeam, LocalDateTime matchDate, int week) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.matchDate = matchDate;
        this.week = week;
        this.isPlayed = false;
        this.homeScore = 0;
        this.awayScore = 0;
    }

    public Match(Team homeTeam, Team awayTeam, int homeScore, int awayScore, LocalDateTime matchDate, boolean isPlayed, int week) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.matchDate = matchDate;
        this.isPlayed = isPlayed;
        this.week = week;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getScore() {
        if (!isPlayed) {
            return "- : -";
        }
        return homeScore + " - " + awayScore;
    }
}
