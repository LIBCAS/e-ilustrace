import Architecture from '../assets/images/architecture.jpg'
import Bible from '../assets/images/bible.jpg'
import Erby from '../assets/images/heraldry.jpg'
import People from '../assets/images/characters.jpg'
import Places from '../assets/images/locations.jpg'
import Objects from '../assets/images/objects.jpg'
import Plants from '../assets/images/plants.jpg'
import Military from '../assets/images/military.jpg'
import Animals from '../assets/images/animals.jpg'

const getThemeImage = (name: string) => {
  let image

  switch (name) {
    case 'Architektura':
      image = Architecture
      break
    case 'Bible':
      image = Bible
      break
    case 'Erby':
      image = Erby
      break
    case 'Lidé a postavy':
      image = People
      break
    case 'Místa':
      image = Places
      break
    case 'Předměty':
      image = Objects
      break
    case 'Rostliny':
      image = Plants
      break
    case 'Vojenství':
      image = Military
      break
    case 'Zvířata':
      image = Animals
      break
    default:
    // image = PhotoIcon
  }

  return image
}

export default getThemeImage
