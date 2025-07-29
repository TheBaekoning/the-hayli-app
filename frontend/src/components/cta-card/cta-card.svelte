<svelte:options runes={true} />
<script lang="ts">
	import type { Snippet } from "svelte";

	let { href, mainCTA = false, children }: { href: string, mainCTA?: boolean, children: Snippet} = $props();
</script>

<a {href} class:mainCTA class="cta-card not-link">
	{@render children()}
</a>

<style lang="scss">
	.cta-card{
		display: grid;
		grid-template-columns: minmax(0, 2fr) minmax(0, 3fr) auto;
		gap: 2px;
		align-items: center;
		padding: 25px 10px;
		color: var(--dark2);
		text-decoration: none;
		border: 1px solid transparent;
		border-radius: 15px;
		background: var(--natural4);
		box-shadow: 0 2px 5px 1px rgba(0, 0, 0, 0.25);
		transition: transform 0.3s ease, box-shadow 0.3s ease;
		&:hover, &:focus{
			transform: translateY(-5px);
			box-shadow: 0 5px 15px 5px rgba(0, 0, 0, 0.25);
		}
		&.mainCTA{
			background: var(--light1);
			:global(.cta-title), :global(.days){
				color: #fff;
			}
			&::after{
				background-image: url(/icons/right-caret-white.svg);
			}
		}
		:global(img){
			display: block;
			width: 100%;
			height: auto;
		}
		:global(.cta-title){
			font: 600 1.5rem var(--ff);
			color: #000;
		}
		:global(.cta-title.big){
			font: 700 1.875rem/1.3 var(--ff);
		}
		:global(.cta-title.small){
			font: 600 1.25rem/1 var(--ff);
		}
		:global(.days){
			width: 70%;
			font: 600 3.4375rem/1 var(--ff);
			color: #000;
			justify-self: end;
		}
		&:has(.days){
			grid-template-columns: repeat(3, auto);
			align-items: center;
		}
		&::after{
			content: '';
			display: block;
			justify-self: end;
			margin-left: 20px;
			width: 0.5625rem;
			height: 0.875rem;
			background: url(/icons/right-caret-black.svg) center no-repeat;
		}
		@media (prefers-color-scheme: dark){
			color: var(--light2);
			background: var(--dark1);
			:global(.cta-title), :global(.days){
				color: var(--light1);
			}
			:global(.link){
				color: var(--light1);
				&:hover{
					color: var(--light1);
				}
			}
			&::after{
				filter: invert(1);
			}
		}
	}
	@container (width > 500px){
		.cta-card{
			&:has(.days){
				padding: 20px 40px;
				grid-template-columns: max-content repeat(2, auto);
				gap: 20px;
			}
		}
	}
</style>