export const getStorage = async (key) => {
  try {
    return await localStorage.getItem(key);
  } catch (error) {
    console.error(`Error getting '${key}' from LocalStorage`);
    console.error(error);
    return null;
  }
};

export const setStorage = async (key, value) => {
  try {
    await localStorage.setItem(key, value);
    return Promise.resolve();
  } catch (error) {
    console.error(`Error setting '${key}' in LocalStorage`);
    console.error(error);
    return null;
  }
};

export const removeStorage = async (key) => {
  try {
    localStorage.removeItem(key);
    return Promise.resolve();
  } catch (error) {
    console.error(`Error removing token ${key} from LocalStorage`);
    console.error(error);
    return null;
  }
};
