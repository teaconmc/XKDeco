from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockFamilyProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/family/block', 'block_families')
        self.data = {}
        self.stonecutterFrom = {}

    def addEntry(self, key: ResourceLocation, value: ResourceLocation):
        if key not in self.data:
            self.data[key] = set()
        self.data[key].add(str(value))

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
        for key, value in self.data.items():
            singleData = {}
            if key in self.stonecutterFrom:
                singleData.update(self.stonecutterFrom[key])
            singleData.update({
                'stonecutter_exchange': True,
                'quick_switch': True,
                'values': sorted(list(value))
            })
            self.writeFile(key, singleData)
