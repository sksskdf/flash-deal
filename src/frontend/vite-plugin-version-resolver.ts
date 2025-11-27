import type { Plugin } from 'vite';

export function versionResolverPlugin(): Plugin {
  return {
    name: 'version-resolver',
    enforce: 'pre',
    resolveId(source, importer) {
      // Handle imports with @version syntax
      const match = source.match(/^(.+)@([\d.]+)$/);
      if (match) {
        const [, packageName] = match;
        return this.resolve(packageName, importer, { skipSelf: true });
      }
      return null;
    },
  };
}
