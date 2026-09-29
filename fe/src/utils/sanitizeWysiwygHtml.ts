import DOMPurify from 'dompurify'

const sanitizeWysiwygHtml = (html: string) => {
  const sanitizedHtml = DOMPurify.sanitize(html)

  if (typeof document === 'undefined') {
    return sanitizedHtml
  }

  const template = document.createElement('template')
  template.innerHTML = sanitizedHtml

  template.content.querySelectorAll('a[href]').forEach((link) => {
    link.setAttribute('target', '_blank')
    link.setAttribute('rel', 'noopener noreferrer')
  })

  return template.innerHTML
}

export default sanitizeWysiwygHtml
