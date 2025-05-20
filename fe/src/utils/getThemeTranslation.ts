const getThemeTranslation = (name: string) => {
  let translatedName = ''

  switch (name) {
    case 'Architektura':
      translatedName = 'Architecture'
      break
    case 'Bible':
      translatedName = 'Bible'
      break
    case 'Erby':
      translatedName = 'Coats of arms'
      break
    case 'Lidé a postavy':
      translatedName = 'People and figures'
      break
    case 'Místa':
      translatedName = 'Places'
      break
    case 'Předměty':
      translatedName = 'Subjects'
      break
    case 'Rostliny':
      translatedName = 'Plants'
      break
    case 'Vojenství':
      translatedName = 'Military'
      break
    case 'Zvířata':
      translatedName = 'Animals'
      break
    default:
  }

  return translatedName
}

export default getThemeTranslation
