from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockFamilyProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/family', 'block_families')
        self.ids = set()
        self.blocks = {}
        self.items = {}
        self.stonecutterFrom = {}

    def addBlock(self, key: ResourceLocation, value: ResourceLocation):
        if key not in self.blocks:
            self.blocks[key] = set()
            self.ids.add(key)
        self.blocks[key].add(str(value))

    def addItem(self, key: ResourceLocation, value: ResourceLocation):
        if key not in self.items:
            self.items[key] = set()
            self.ids.add(key)
        self.items[key].add(str(value))

    def generateRow(self, row, csvConfig):
        familyId = self.pack.defaultResourceLocation(row['ID'])
        data = {}
        if 'StonecutterFrom' in row and row['StonecutterFrom'] != '':
            data['stonecutter_from'] = row['StonecutterFrom']
        if 'InputsInViewer' in row and row['InputsInViewer'] != '':
            inputs = []
            for value in row['InputsInViewer'].split(','):
                inputs.append(str(self.pack.defaultResourceLocation(value.strip())))
            if len(inputs) == 1:
                data['exchange_inputs_in_viewer'] = inputs[0]
            else:
                data['exchange_inputs_in_viewer'] = inputs
        self.stonecutterFrom[familyId] = data

    def generate(self):
        super().generate()
        for key in self.ids:
            singleData = {}
            if key in self.stonecutterFrom:
                singleData.update(self.stonecutterFrom[key])
            singleData.update({
                'stonecutter_exchange': True,
                'quick_switch': True
            })
            if key in self.blocks:
                singleData['blocks'] = sorted(list(self.blocks[key]))
            if key in self.items:
                singleData['items'] = sorted(list(self.items[key]))
            self.writeFile(key, singleData)
