import yaml

from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class SoundEventProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/sound_event', 'sound_events')

    def generateRow(self, row, tableConfig):
        data = {}

        self.field(data, 'Range', float)

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)
