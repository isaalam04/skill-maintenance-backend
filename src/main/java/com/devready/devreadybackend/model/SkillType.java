package com.devready.devreadybackend.model;

// an enum is a fixed list of allowed values — we use it to categorise skills
// this stops anyone passing in a random string like "piano" as a skill type
public enum SkillType {
    LANGUAGE,       // e.g. urdu, french, mandarin — decays the fastest
    TECHNICAL,      // e.g. java, python, sql — decays medium-fast
    THEORETICAL,    // e.g. algorithms, maths, theory — decays medium
    PHYSICAL        // e.g. guitar, sport, gym — decays slowest (muscle memory)
}