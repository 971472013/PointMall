import Cookies from 'js-cookie'

export function getV(name) {
  return Cookies.get(name)
}

export function getJSON(name) {
  return Cookies.getJSON(name)
}

export function setV(name, v) {
  return Cookies.set(name, v)
}

export function removeV(name) {
  return Cookies.remove(name)
}

