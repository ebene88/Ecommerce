import createStore from "@/lib/createStore";

export type Theme = "dark" | "light";

export const {
  StoreProvider: ThemeProvider,
  useDispatch,
  useStore,
} = createStore(
  (_, next: Theme) => {
    localStorage.setItem("theme", next);
    return next as Theme;
  },
  localStorage.getItem("theme") !== null
    ? (localStorage.getItem("theme") as Theme)
    : window.matchMedia("(prefers-color-scheme: dark)").matches
    ? "dark"
    : "light"
);

export function useThemes() {
  return {
    theme: useStore(),
    setTheme: useDispatch(),
  };
}
