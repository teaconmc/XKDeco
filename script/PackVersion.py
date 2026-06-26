import TagsProvider
from DataProvider import DataProvider
from TagsProvider import TagsProvider


class PackVersion:

    def __init__(self, version: str):
        self.version = version
        self.extraFeatures = set()

    def resolvePath(self, pack, data_provider: DataProvider) -> str:
        return data_provider.dataPath

    def __str__(self):
        return self.version


class PackVersion26(PackVersion):
    pass


class PackVersion21(PackVersion26):
    def __init__(self, version: str):
        super().__init__(version)
        self.extraFeatures.add('render_type')


class PackVersion20(PackVersion21):
    def resolvePath(self, pack, data_provider: DataProvider) -> str:
        if isinstance(data_provider, TagsProvider):
            if data_provider.dataPath.endswith("/tags/item"):
                return data_provider.dataPath.replace("/tags/item", "/tags/items")
            if data_provider.dataPath.endswith("/tags/block"):
                return data_provider.dataPath.replace("/tags/block", "/tags/blocks")
            if data_provider.dataPath.endswith("/tags/fluid"):
                return data_provider.dataPath.replace("/tags/fluid", "/tags/fluids")
            if data_provider.dataPath.endswith("/tags/entity_type"):
                return data_provider.dataPath.replace(
                    "/tags/entity_type", "/tags/entity_types"
                )
            if data_provider.dataPath.endswith("/tags/game_event"):
                return data_provider.dataPath.replace(
                    "/tags/game_event", "/tags/game_events"
                )
        return data_provider.dataPath


VERSIONS = {
    "1.20": PackVersion20("1.20"),
    "1.21": PackVersion21("1.21"),
    "26.1": PackVersion26("26.1"),
}


def get(version: str):
    if version in VERSIONS:
        return VERSIONS[version]
    raise ValueError("Unsupported data pack version: " + version)
