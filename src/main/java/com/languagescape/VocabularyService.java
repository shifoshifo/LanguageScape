
package com.languagescape;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VocabularyService
{
    // Stores vocabulary entries using OSRS item names.
    private final Map<String, VocabularyEntry> vocabulary =
            new HashMap<>();

    public VocabularyService()
    {
        // Our first three vocabulary entries.
        addWord(
                "Logs",
                "Troncos",
                "حطب",
                "hatab"
        );

        addWord(
                "Oak logs",
                "Troncos de roble",
                "خشب البلوط",
                "khashab al-ballut"
        );

        addWord(
                "Iron ore",
                "Mineral de hierro",
                "خام الحديد",
                "kham al-hadid"
        );
    }

    private void addWord(
            String itemName,
            String spanish,
            String arabic,
            String transliteration)
    {
        vocabulary.put(
                itemName.toLowerCase(Locale.ROOT),
                new VocabularyEntry(
                        spanish,
                        arabic,
                        transliteration
                )
        );
    }

    public VocabularyEntry findWord(String itemName)
    {
        return vocabulary.get(
                itemName.toLowerCase(Locale.ROOT)
        );
    }

    public static class VocabularyEntry
    {
        public final String spanish;
        public final String arabic;
        public final String transliteration;

        public VocabularyEntry(
                String spanish,
                String arabic,
                String transliteration)
        {
            this.spanish = spanish;
            this.arabic = arabic;
            this.transliteration = transliteration;
        }
    }
}
