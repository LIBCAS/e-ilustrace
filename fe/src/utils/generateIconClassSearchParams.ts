import { TIconClassVariablesState } from '../store/useIconClassStore'

type TReturnObjectStructure = {
  page?: string
  search?: string
  category?: string
  icc?: string
}

const generateIconClassSearchParams = ({
  page,
  search,
  category,
  icc,
}: Partial<TIconClassVariablesState>) => {
  const returnObject: TReturnObjectStructure = {}

  if (page) {
    returnObject.page = page.toString()
  }

  if (search) {
    returnObject.search = search
  }

  if (category) {
    returnObject.category = `${category.value}~${category.label}~${category.operation}`
  }

  if (icc) {
    returnObject.icc = icc.join(';')
  }

  return returnObject
}

export default generateIconClassSearchParams
