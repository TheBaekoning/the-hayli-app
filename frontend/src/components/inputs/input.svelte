<script lang="ts">
	import { v4 } from 'uuid';

	let {
		label = '',
		name = '',
		required = false,
		id = v4(),
		errorMessage = '',
		value = $bindable(''),
		type = 'text',
		inputEventCallback = null,
		maxlength = null,
		...inputProps
	} : {
		label?: string,
		name?: string,
		required?: boolean,
		id?: string,
		errorMessage?: string,
		value?: any,
		type?: 'text' | 'email' | 'password' | 'number' | 'date' | 'textarea' | 'mood',
		inputEventCallback?: (e: Event) => void,
		maxlength?: number,
		inputProps?: any
	} = $props();
	
	let originalType = type;

	// Label active class
	let small: boolean = $state(originalType === 'date' || !!value);
	let input: HTMLInputElement | HTMLTextAreaElement | null = $state(null);

	let showPassword: boolean = $state(false);

	let derivedLabel = $derived.by(() => {
		let defaultLabel = '';
		switch (originalType) {
			case 'email':
				defaultLabel = 'Email';
				break;
			case 'password':
				defaultLabel = 'Password';
				break;
			case 'mood':
				defaultLabel = 'Mood';
				break;
			default:
				defaultLabel = label;
		}
		return label || defaultLabel;
	});
	let derivedName = $derived.by(() => {
		let defaultName = '';
		switch (originalType) {
			case 'email':
				defaultName = 'email';
				break;
			case 'password':
				defaultName = 'password';
				break;
			case 'mood':
				defaultName = 'moodRating';
				break;
			default:
				defaultName = name;
		}
		return name || defaultName;
	});

	// Mood checked attrs
	let vgChecked = $derived(type === 'mood' && parseInt(value) === 5);
	let gChecked = $derived(type === 'mood' && parseInt(value) === 4);
	let nChecked = $derived(type === 'mood' && parseInt(value) === 3);
	let bChecked = $derived(type === 'mood' && parseInt(value) === 2);
	let vbChecked = $derived(type === 'mood' && parseInt(value) === 1);

	function updateLabelDisplay() {
		if (originalType === 'date') return;
		small = !!value;
	}

	function onInput(e) {
		switch (type) {
			case 'number':
				value = Number(value);
				break;
			default:
				value = e.target.value;
		}
		if (typeof inputEventCallback === 'function') inputEventCallback(e);
	}

	function togglePasswordVisibility() {
		showPassword = !showPassword;
		type = showPassword ? 'text' : 'password';
	}

	function updateTextareaHeight() {
		input.style.height = '1px';
		input.style.height = `${25 + input.scrollHeight}px`;
	}
	$effect(() => {
		if (!!value){
			updateLabelDisplay();
			if (originalType === 'textarea') updateTextareaHeight();
		}
	});
</script>
<svelte:options runes={true} />

<div class="input-group" class:email={originalType === 'email'} class:password={originalType === 'password'} class:mood={originalType === 'mood'}>
	<div class="error-message">{errorMessage}</div>
	{#if type === 'mood'}
		<fieldset {id}>
			<legend>Mood</legend>
			<label for="mood-5">
				<span aria-hidden="true">😁</span>
				<span class="visually-hidden">Very Good</span>
				<input type="radio" id="mood-5" name="moodRating" value="5" required class="visually-hidden" checked={vgChecked} />
			</label>
			<label for="mood-4">
				<span aria-hidden="true">😊</span>
				<span class="visually-hidden">Good</span>
				<input type="radio" id="mood-4" name="moodRating" value="4" required class="visually-hidden" checked={gChecked} />
			</label>
			<label for="mood-3">
				<span aria-hidden="true">😐</span>
				<span class="visually-hidden">Neutral</span>
				<input type="radio" id="mood-3" name="moodRating" value="3" required class="visually-hidden" checked={nChecked} />
			</label>
			<label for="mood-2">
				<span aria-hidden="true">😕</span>
				<span class="visually-hidden">Bad</span>
				<input type="radio" id="mood-2" name="moodRating" value="2" required class="visually-hidden" checked={bChecked} />
			</label>
			<label for="mood-1">
				<span aria-hidden="true">😢</span>
				<span class="visually-hidden">Very Bad</span>
				<input type="radio" id="mood-1" name="moodRating" value="1" required class="visually-hidden" checked={vbChecked} />
			</label>
		</fieldset>
	{:else}
		<label for={id} class:small>{derivedLabel}</label>
		{#if type === 'textarea'}
			<textarea
				bind:this={input}
				{id}
				name={derivedName}
				{required}
				onblur={updateLabelDisplay}
				oninput={onInput}
				onkeyup={updateTextareaHeight}
				{...inputProps}
			>{value}</textarea>
		{:else}
			<input
				bind:this={input}
				{id}
				name={derivedName}
				{type}
				{required}
				onblur={updateLabelDisplay}
				{value}
				oninput={onInput}
				{...inputProps}
			/>
		{/if}
	{/if}
	{#if originalType === 'password'}
		<button type="button" class="show-password not-button" onclick={togglePasswordVisibility}>
			<img
				alt={showPassword ? 'Hide password' : 'Show password'}
				src="/icons/hidden.svg"
				width="24"
				height="24"
				loading="lazy"
				decoding="async"
			/>
		</button>
	{/if}
</div>

<style lang="scss">
	.input-group {
		position: relative;
		display: flex;
		gap: 10px;
		padding: 16px 25px;
		width: calc(100% - 50px);
		border: 1px solid var(--dark2);
		border-radius: 5px;
		background: #fff;
		%small-label {
			margin-top: -16px;
			margin-left: -10px;
			transform: scale(0.75);
		}
		label {
			position: absolute;
			display: flex;
			align-items: center;
			gap: 15px;
			margin-top: 3px;
			font-size: 0.875rem;
			color: var(--dark2);
			filter: opacity(0.7);
			transition: all 0.3s ease;
			cursor: pointer;
			&.small {
				@extend %small-label;
			}
		}
		&:focus-within {
			label {
				@extend %small-label;
			}
		}
		input {
			flex: 0 1 auto;
			background: none;
			border: none;
			padding: 5px 0;
			width: 100%;
			&:focus {
				outline: none;
			}
		}
		textarea{
			background: none;
			border: none;
			width: 100%;
			&:focus{
				outline: none;
			}
		}
		.error-message {
			position: absolute;
			left: 0;
			top: -15px;
			font-size: 0.75rem;
			color: red;
			filter: opacity(0.7);
		}
		&.email {
			label {
				&::before {
					content: '';
					display: block;
					width: 24px;
					height: 24px;
					background: url(/icons/email.svg) center/cover no-repeat;
				}
			}
		}
		&.password{
			label{
				&::before{
					content: '';
					display: block;
					width: 24px;
					height: 24px;
					background: url(/icons/password.svg) center/cover no-repeat;
				}
			}
			.show-password{
				flex: 1 0 auto;
				display: block;
			}
		}
		&.mood{
			fieldset{
				display: grid;
				grid-template-columns: repeat(5, 1fr);
				gap: 30px;
				padding: 0;
				width: 100%;
				border: none;
				legend{
					grid-column: 1 / 6;
					margin-bottom: 10px;
				}
			}
			label{
				flex: 1 1 auto;
				position: static;
				margin-top: 0;
				width: fit-content;
				font-size: 1.4rem;
				text-align: center;
				border-bottom: 3px solid transparent;
				transition: border-color 0.3s ease;
				&:has(:checked){
					border-bottom-color: var(--dark2);
				}
				input{
					font-size: 0.5em;
					width: 0.5em;
				}
			}
			&:focus-within{
				label{
					margin-top: 0;
					margin-left: 0;
					transform: none;
				}
			}
			@media (prefers-color-scheme: dark){
				fieldset{
					legend{
						color: var(--dark2);
					}
				}
			}
		}
	}
</style>
