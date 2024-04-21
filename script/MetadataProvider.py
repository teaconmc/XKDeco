from typing import List

from DataProvider import DataProvider


class MetadataProvider(DataProvider):
    def __init__(self, pack):
        super().__init__(pack, 'metadata', 'assets/{}')
        self.data = {}
        self.prettyPrint = True

    def putRegistryOrder(self, key: str, values: List[str]):
        if 'registry_order' not in self.data:
            self.data['registry_order'] = {}
        registryOrder = self.data['registry_order']
        if key not in registryOrder:
            registryOrder[key] = []
        registryOrder[key].extend(values)

    def generate(self):
        self.writeFile(self.pack.defaultResourceLocation('metadata'), self.data)
