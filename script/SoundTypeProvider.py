import yaml

from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class SoundTypeProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/sound_type', 'sound_types')
        self.mappings = {
            'BreakSound': 'break',
            'StepSound': 'step',
            'PlaceSound': 'place',
            'HitSound': 'hit',
            'FallSound': 'fall'
        }

    def generateRow(self, row, tableConfig):
        data = {}

        for key, value in self.mappings.items():
            if key in row and row[key] != '':
                data[value] = self.pack.defaultResourceLocation(row[key])

        self.field(data,'Volume', float)
        self.field(data,'Pitch', float)

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)
