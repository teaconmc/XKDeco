import re

namespacePattern = re.compile(r'^[a-z0-9_\-]+')
pathPattern = re.compile(r'^[a-z0-9_\-/]+')

class ResourceLocation:
    def __init__(self, string: str, string2: str = None):
        if string2 is not None:
            self.namespace = string
            self.path = string2
            return
        parts = string.split(':')
        if len(parts) == 1:
            self.namespace = 'minecraft'
            self.path = parts[0]
        elif len(parts) == 2:
            self.namespace = parts[0]
            self.path = parts[1]
        else:
            raise ValueError('Invalid ResourceLocation string: ' + string)
        if not namespacePattern.match(self.namespace):
            raise ValueError('Invalid namespace: "' + self.namespace + '"')
        if not pathPattern.match(self.path):
            raise ValueError('Invalid path: "' + self.path + '"')

    def __hash__(self):
        return hash((self.namespace, self.path))

    def __eq__(self, other):
        return self.namespace == other.namespace and self.path == other.path

    def __str__(self):
        return self.namespace + ':' + self.path

    def __repr__(self):
        return 'L\'' + str(self) + '\''
