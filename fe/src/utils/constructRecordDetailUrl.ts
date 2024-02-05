const constructRecordDetailUrl = (path: string | number, backPath?: string) => {
  return `/record-detail/${path}${backPath ? `?back=${backPath}` : ''}`
}

export default constructRecordDetailUrl
