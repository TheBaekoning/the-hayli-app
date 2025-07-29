<svelte:options runes={true} />
<script lang="ts">
	import { EButtonType } from "$lib";
	import type { Snippet } from "svelte";
	let { type = EButtonType.link, children, ...restProps }: {type: EButtonType, children: Snippet } & Record<any,any> = $props();
</script>
{#if type === EButtonType.link}
	<a href="/404" {...restProps}>{@render children()}</a>
{:else if type === EButtonType.button}
	<button {...restProps}>{@render children()}</button>
{:else}
	<button type={type} {...restProps}>{@render children()}</button>
{/if}
<style lang="scss">
	a, button{
		display: block;
		--pad: 16px;
		padding: var(--pad);
		width: 100%;
		border-radius: 32px;
		font: 500 1rem var(--ff);
		color: #000;
		text-align: center;
		text-decoration: none;
		background: var(--light1);
		cursor: pointer;
		outline: inherit;
		border: 1px solid var(--light1);
		&.secondary{
			background: #fff;
			border: 1px solid #e8e4d9;
		}
	}
	@media (prefers-color-scheme: dark){
		a, button{
			&:hover, &:focus{
				color: #000;
			}
			&.secondary{
				color: #000;
			}
		}
	}
</style>