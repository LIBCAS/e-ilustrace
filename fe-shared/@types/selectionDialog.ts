import type { Backend } from "./endpoints";

interface TExploreBook extends Backend.Book {
  selected: boolean;
}

interface TExploreIllustration extends Backend.Illustration {
  selected: boolean;
}

export interface TExploreSelection {
  books: TExploreBook[];
  illustrations: TExploreIllustration[];
}
