<svelte:options runes={true} />
<script lang="ts">
	import type { Snippet } from "svelte";
	let { active, hidden, children }: { active: boolean, hidden: boolean, children?: Snippet } = $props();
</script>

<div class="slide" class:active {hidden}>
	{@render children()}
</div>

<style lang="scss">
	.slide{
		display: flex;
		flex-flow: column;
		justify-content: end;
		height: 100%;
		filter: opacity(0);
		transition: 0.3s ease;
		&.active{
			filter: opacity(1);
		}
	}
</style>