import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_protected/product/my-product')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/_protected/product/my"!</div>
}
