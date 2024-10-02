import json
import os
from pathlib import Path

from DataProvider import DataProvider


class TranslationProvider(DataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'translations', 'assets/{}/lang')
        self.data = {}
        self.prettyPrint = True
        self.exportFormat = 'json'

    def putTranslation(self, lang: str, key: str, value: str):
        if lang not in self.data:
            self.data[lang] = {}
        self.data[lang][key] = value

    def removeTranslation(self, key: str):
        for lang in self.data:
            if key in self.data[lang]:
                del self.data[lang][key]

    def generate(self):
        format: str = self.pack.config['translation_file_name']
        assert format.endswith('.json'), 'Translation file name must end with .json'
        format = format[:-5]
        for lang, translations in self.data.items():
            translations = dict(sorted(translations.items()))
            self.writeFile(self.pack.defaultResourceLocation(format.format(lang)), translations)

    def writeFile(self, file, data: dict):
        if 'translation_dest' in self.pack.config:
            file = os.path.join(self.pack.config['translation_dest'], file.path + '.json')
        else:
            file = self.pack.toAbsPath(self, file, '.json')
        Path(file).parent.mkdir(parents=True, exist_ok=True)
        with open(file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2 if self.prettyPrint else None, ensure_ascii=False)
            self.count += 1
