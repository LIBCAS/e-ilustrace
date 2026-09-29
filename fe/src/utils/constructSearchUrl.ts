const constructSearchUrl = (params?: string) => {
  return `/search${params ? `?${params}` : ''}`
}

export default constructSearchUrl
