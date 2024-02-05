import { TSearchVariablesState } from '../store/useSearchStore'

type TReturnObjectStructure = {
  sort?: string
  year?: string
  itemsPerPage?: string
  view?: string
  type?: string
  filterAuthor?: string
  filterObject?: string
  filterPublishingPlace?: string
  filterSubjectPlace?: string
  currentPage?: string
  searchWithCategory?: string
  IIIFFormat?: string
}

const generateSearchSearchParams = ({
  sort,
  year,
  itemsPerPage,
  view,
  type,
  filterAuthor,
  filterObject,
  filterPublishingPlace,
  filterSubjectPlace,
  currentPage,
  searchWithCategory,
  IIIFFormat,
}: Partial<TSearchVariablesState>) => {
  const returnObject: TReturnObjectStructure = {}

  if (sort) {
    returnObject.sort = `${sort.value}~${sort.label}`
  }

  if (year) {
    returnObject.year = `${year.from}~${year.to}`
  }

  if (itemsPerPage) {
    returnObject.itemsPerPage = `${itemsPerPage.value}~${itemsPerPage.label}`
  }

  if (view) {
    returnObject.view = view
  }

  if (type) {
    returnObject.type = type
  }

  if (filterAuthor?.length) {
    returnObject.filterAuthor = filterAuthor
      .map((fa) => `${fa.value}~${fa.label}`)
      .join(';')
  }

  if (filterObject?.length) {
    returnObject.filterObject = filterObject
      .map((fo) => `${fo.value}~${fo.label}`)
      .join(';')
  }

  if (filterPublishingPlace?.length) {
    returnObject.filterPublishingPlace = filterPublishingPlace
      .map((fp) => `${fp.value}~${fp.label}`)
      .join(';')
  }

  if (filterSubjectPlace) {
    returnObject.filterSubjectPlace = filterSubjectPlace
      .map((fs) => `${fs.value}~${fs.label}`)
      .join(';')
  }

  if (currentPage !== undefined) {
    returnObject.currentPage = currentPage.toString()
  }

  if (searchWithCategory?.length) {
    returnObject.searchWithCategory = searchWithCategory
      .map(
        (sc) =>
          `${sc.search}~${sc.category.value}~${sc.category.label}~${sc.category.operation}~${sc.uuid}`
      )
      .join(';')
  }

  if (IIIFFormat !== undefined) {
    returnObject.IIIFFormat = IIIFFormat ? 'true' : 'false'
  }

  return returnObject
}

export default generateSearchSearchParams
