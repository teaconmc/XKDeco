from Pack import Pack
from Identifier import Identifier
from TableDataProvider import TableDataProvider


class BlockFamilyProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/family', 'block_families')
        self.ids = set()
        self.blocks = {}
        self.items = {}
        self.stonecutterFrom = {}

    def addBlock(self, key: Identifier, value: Identifier):
        if key not in self.blocks:
            self.blocks[key] = set()
            self.ids.add(key)
        self.blocks[key].add(str(value))

    def addItem(self, key: Identifier, value: Identifier):
        if key not in self.items:
            self.items[key] = set()
            self.ids.add(key)
        self.items[key].add(str(value))

    def generateRow(self, row, tableConfig):
        familyId = self.pack.defaultIdentifier(row['ID'])
        data = {}
        if 'StonecutterFrom' in row and row['StonecutterFrom'] != '':
            data['stonecutter_from'] = row['StonecutterFrom']
            if 'StonecutterFromMultiplier' in row and row['StonecutterFromMultiplier'] != '':
                data['stonecutter_from_multiplier'] = int(float(row['StonecutterFromMultiplier']))
        if 'InputsInViewer' in row and row['InputsInViewer'] != '':
            inputs = []
            for value in row['InputsInViewer'].split(','):
                inputs.append(str(self.pack.defaultIdentifier(value.strip())))
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
                'switch': {}
            })
            if key in self.blocks:
                singleData['blocks'] = sorted(list(self.blocks[key]))
            if key in self.items:
                singleData['items'] = sorted(list(self.items[key]))
            self.writeFile(key, singleData)
