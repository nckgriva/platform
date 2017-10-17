package com.gracelogic.platform.localization.service;

import java.util.Locale;

public class TestCls {
    public static void main(String ... args) {
        String source = "i18n{\"ru\":\"\n" +
                "<h4>C Major Triads - Narrow Position</ h4>\n" +
                "<br>\n" +
                "(До мажорные трезвучия - узкое расположение)\n" +
                "<br>\n" +
                "<br>\n" +
                "Здравствуйте.\n" +
                "<br>\n" +
                "<br>\n" +
                "В нашем 1-м уроке мы поговорим о мажорных трезвучиях в узком расположении в тональности До мажор (C).\n" +
                "<br>\n" +
                "<br>\n" +
                "Как вы уже догадались из названия, трезвучия - это звукоряд состоящий из 3-х звуков. Трезвучия - это скелет аккорда, его основа, то, что определяет  тип аккорда (мажор, минор и т.д.). \n" +
                "<br>\n" +
                "<br>\n" +
                "До мажорное трезвучие состоит из нот - До, Ми, Соль.\n" +
                "<br>\n" +
                "<br>\n" +
                "Вам нужно выучить урок и сделать домашнее задание.\n" +
                "<br>\n" +
                "<br>\n" +
                "<span class=\\\"mtd-homework\\\">ДОМАШНЕЕ ЗАДАНИЕ: </span>\n" +
                "<br>\n" +
                "Играть в тональности D \n" +
                "<br>\n" +
                "<span class=\\\"mtd-hint\\\">ПОДСКАЗКА:</span>\n" +
                "<br>\n" +
                "Нота D отстоит на большую секунду (2 лада) выше от ноты С\n" +
                "\n" +
                "\",\n" +
                "\"en\":\"\n" +
                "<h4>C Major Triads – Narrow Position</ h4>\n" +
                "<br>\n" +
                "<br>\n" +
                "Hello. \n" +
                "<br>\n" +
                "<br>\n" +
                "In our first lesson, we will talk about major triads in narrow position in the key of C major.\n" +
                "<br>\n" +
                "<br>\n" +
                "As you have already understood from the title, the triad is a scale consisting of three tones. Triads are the chord skeleton, its basis, what defines the chord type (major, minor, etc.).\n" +
                "<br>\n" +
                "<br>\n" +
                "С major triad consists of notes C, E, and G. You need to learn the lesson and do homework.\n" +
                "<br>\n" +
                "<br>\n" +
                "<span class=\\\"mtd-homework\\\">HOMEWORK:</span>\n" +
                "<br>\n" +
                "Play in D key\n" +
                "<br>\n" +
                "<span class=\\\"mtd-hint\\\">HINT:</span>\n" +
                "<br>\n" +
                "The note D is above the note C on a major second (2 modes)\n" +
                "\"}";
        String result = StringConverter.getInstance().process(source, Locale.forLanguageTag("ru"));
        System.out.println(result);
    }
}
